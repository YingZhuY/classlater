/*
 * mm-naive.c - The fastest, least memory-efficient malloc package.
 *
 * In this naive approach, a block is allocated by simply incrementing
 * the brk pointer.  A block is pure payload. There are no headers or
 * footers.  Blocks are never coalesced or reused. Realloc is
 * implemented directly using mm_malloc and mm_free.
 *
 * NOTE TO STUDENTS: Replace this header comment with your own header
 * comment that gives a high level description of your solution.
 */

/*
 * mm-seg.c - The segregated-fit, LIFO explcit free list based malloc package with better realloc (93/100).
 *
 * in 2017 Fall System Programming class in SNU
 * by Seungwoo Lee
 * 2017/11/14
 *
 * in this malloc-lab, our memory model is 32-bit
 *
 * overall heap structure:
 *  - heap contains several segregated lists
 *  - the number of seg-list is SEGLIST_COUNT(now 13)
 *  - each seglist contains several free blocks,
 *  - whose size are 2^4..2^5-1, 2^5..2^6-1, ... 2^15..2^16-1, 2^16..inf
 *  - there are SEGLIST_COUNT prolog blocks at the heap start,
 *  - and, also SEGLIST_COUNT epilog blocks at the heap end.
 *  - between prologs and epilogs, there are N>=0 normal blocks.
 *
 * normal block structure
 *  - a normal block is one of allocated block or free block
 *  - every normal block has own header at the start and footer at the end.
 *  - header/footer contains block size and one free bit.
 *  - block size includes header and footer size
 *  - free bit is set in case of free blocks, while not set in the allocated ones.
 *  - a free block contains PRED and SUCC pointer,
 *  - which points to it's predecessor and successor free block respectively.
 *  - minimum block size is 16 byte
 *
 * prolog/epilog block structure
 *  - one prolog block contains a PRED(always NULL), a SUCC, a footer(0).
 *  - one epilog block contains a header(0), a PRED, a SUCC(always NULL).
 *  - header and footer set to 0 as it is neither an allocated block, nor a free one.
 *
 * seg-list structure
 *  - each seg-list is doubly linked list of free blocks
 *  - there are prolog and epilog block on the each end of the seg-list.
 *  - at first, each seg-list contains prolog and epilog block.
 *  - we insert block into the seg-list corresponding to the block size.
 *
 */

#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <unistd.h>
#include <string.h>

#include "mm.h"
#include "memlib.h"

/*********************************************************
 * NOTE TO STUDENTS: Before you do anything else, please
 * provide your team information in the following struct.
 ********************************************************/
team_t team = {
    /* Team name */
    "burning red",
    /* First member's full name */
    "red",
    /* First member's email address */
    "red@163.com",
    /* Second member's full name (leave blank if none) */
    "burning",
    /* Second member's email address (leave blank if none) */
    "burning@163.com"
};

#define WSIZE 4
#define DSIZE 8
#define CHUNKSIZE (1<<12)
#define MAX(x,y) ((x)>(y)?(x):(y))
#define PACK(size,alloc) ((size)|(alloc))
#define GET(p)     (*(unsigned int *)(p))
#define PUT(p,val) (*(unsigned int *)(p) = (val))
#define GET_SIZE(p)  (GET(p) & ~0x7)
#define GET_ALLOC(p) (GET(p) & 0x1)
#define HDRP(bp) ((char *)(bp) - WSIZE)
#define FTRP(bp) ((char *)(bp) + GET_SIZE(HDRP(bp)) - DSIZE)
#define NEXT_BLKP(bp) ((char *)(bp) + GET_SIZE(((char *)(bp) - WSIZE)))
#define PREV_BLKP(bp) ((char *)(bp) - GET_SIZE(((char *)(bp) - DSIZE)))
extern void *extend_heap(size_t words);
extern void *coalesce(void *bp);
extern void *find_fit(size_t size);
extern void place(void *bp,size_t asize);
extern int mm_init (void);
extern void *mm_malloc (size_t size);
extern void mm_free (void *ptr);
extern void *mm_realloc(void *ptr, size_t size);

static char *heap_listp = 0;

int mm_init(void)
{
    if((heap_listp = mem_sbrk(4*WSIZE))==(void *)-1){
        return -1;
    }
    PUT(heap_listp,0);
    PUT(heap_listp+(1*WSIZE),PACK(DSIZE,1));
    PUT(heap_listp+(2*WSIZE),PACK(DSIZE,1));
    PUT(heap_listp+(3*WSIZE),PACK(0,1));
    heap_listp += (2*WSIZE);
    if(extend_heap(CHUNKSIZE/WSIZE)==NULL){
        return -1;
    }
    return 0;
}

void *extend_heap(size_t words){
    char *bp;
    size_t size;
    size = (words%2) ? (words+1)*WSIZE : words*WSIZE;
    if((long)(bp=mem_sbrk(size))==(void *)-1)
        return NULL;
    PUT(HDRP(bp),PACK(size,0));
    PUT(FTRP(bp),PACK(size,0));
    PUT(HDRP(NEXT_BLKP(bp)),PACK(0,1));
    return coalesce(bp);
}

void *mm_malloc(size_t size)
{
    size_t asize;
    size_t extendsize;
    char *bp;
    if(size ==0) return NULL;
    if(size <= DSIZE){
        asize = 2*(DSIZE);
    }else{
        asize = (DSIZE)*((size+(DSIZE)+(DSIZE-1)) / (DSIZE));
    }
    if((bp = find_fit(asize))!= NULL){
        place(bp,asize);
        return bp;
    }
    extendsize = MAX(asize,CHUNKSIZE);
    if((bp = extend_heap(extendsize/WSIZE))==NULL){
        return NULL;
    }
    place(bp,asize);
    return bp;
}

void mm_free(void *bp)
{
    if(bp == 0)
    return;
    size_t size = GET_SIZE(HDRP(bp));
    PUT(HDRP(bp), PACK(size, 0));
    PUT(FTRP(bp), PACK(size, 0));
    coalesce(bp);
}

void *coalesce(void *bp){
    size_t  prev_alloc = GET_ALLOC(FTRP(PREV_BLKP(bp)));
    size_t  next_alloc = GET_ALLOC(HDRP(NEXT_BLKP(bp)));
    size_t size = GET_SIZE(HDRP(bp));
    if(prev_alloc && next_alloc) {
        return bp;
    }else if(prev_alloc && !next_alloc){
            size += GET_SIZE(HDRP(NEXT_BLKP(bp)));
            PUT(HDRP(bp), PACK(size,0));
            PUT(FTRP(bp), PACK(size,0));
    }else if(!prev_alloc && next_alloc){
        size += GET_SIZE(HDRP(PREV_BLKP(bp)));
        PUT(FTRP(bp),PACK(size,0));
        PUT(HDRP(PREV_BLKP(bp)),PACK(size,0));
        bp = PREV_BLKP(bp);
    }else {
        size +=GET_SIZE(FTRP(NEXT_BLKP(bp)))+ GET_SIZE(HDRP(PREV_BLKP(bp)));
        PUT(FTRP(NEXT_BLKP(bp)),PACK(size,0));
        PUT(HDRP(PREV_BLKP(bp)),PACK(size,0));
        bp = PREV_BLKP(bp);
    }
    return bp;
}

void *find_fit(size_t size){
    void *bp;
    for(bp = heap_listp; GET_SIZE(HDRP(bp))>0; bp = NEXT_BLKP(bp)){
        if(!GET_ALLOC(HDRP(bp)) && (size <= GET_SIZE(HDRP(bp)))){
            return bp;
        }
    }
    return NULL;
}

void place(void *bp,size_t asize){
    size_t csize = GET_SIZE(HDRP(bp));
    if((csize-asize)>=(2*DSIZE)){
        PUT(HDRP(bp),PACK(asize,1));
        PUT(FTRP(bp),PACK(asize,1));
        bp = NEXT_BLKP(bp);
        PUT(HDRP(bp),PACK(csize-asize,0));
        PUT(FTRP(bp),PACK(csize-asize,0));
    }else{
        PUT(HDRP(bp),PACK(csize,1));
        PUT(FTRP(bp),PACK(csize,1));
    }
}

void *mm_realloc(void *ptr, size_t size)
{
    size_t oldsize;
    void *newptr;
    /* If size == 0 then this is just free, and we return NULL. */
    if(size == 0) {
    mm_free(ptr);
    return 0;
    }
    /* If oldptr is NULL, then this is just malloc. */
    if(ptr == NULL) {
    return mm_malloc(size);
    }
    newptr = mm_malloc(size);
    /* If realloc() fails the original block is left untouched  */
    if(!newptr) {
    return 0;
    }
    /* Copy the old data. */
    oldsize = GET_SIZE(HDRP(ptr));
    if(size < oldsize) oldsize = size;
    memcpy(newptr, ptr, oldsize);
    /* Free the old block. */
    mm_free(ptr);
    return newptr;
}
