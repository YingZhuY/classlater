/********************************************************
 * Kernels to be optimized for the CS:APP Performance Lab
 ********************************************************/

#include <stdio.h>
#include <stdlib.h>
#include "defs.h"

/* 
 * Please fill in the following team struct 
 */
team_t team = {
    "red",              /* Team name */

    "Harry Q. Bovik",     /* First member full name */
    "bovik@nowhere.edu",  /* First member email address */

    "red",                   /* Second member full name (leave blank if none) */
    "burning red"                    /* Second member email addr (leave blank if none) */
};

/***************
 * ROTATE KERNEL
 ***************/

/******************************************************
 * Your different versions of the rotate kernel go here
 ******************************************************/

/* 
 * naive_rotate - The naive baseline version of rotate 
 */
char naive_rotate_descr[] = "naive_rotate: Naive baseline implementation";
void naive_rotate(int dim, pixel *src, pixel *dst) 
{
    int i, j;
    //I. Transpose: 对第(i,j)个像素对，M_ij 和 M_ji 交换 II. Exchange rows：行 i 和行 N-1-i 交换
    for (i = 0; i < dim; i++)
	for (j = 0; j < dim; j++)
	    dst[RIDX(dim-1-j, i, dim)] = src[RIDX(i, j, dim)];
}

void my_rotate(int dim, pixel *src, pixel *dst)
{
    int i,j,m,n;
    for(i=0; i<dim; i+=8){
        for(j=0; j<dim; j+=8){
            // 分块
            for(m=i; m<i+8; m++){
                for(n=j; n<j+8; n++){
                    dst[RIDX(dim-1-n, m, dim)]=src[RIDX(m, n, dim)];
                }
                //for(n=m; n<i+8; n++) 
                //    dst[RIDX(dim-1-n, m+j-i, dim)]=src[RIDX(m+j-i, n, dim)];
                //for(n=m-1; n>=i; n--)
                //    dst[RIDX(dim-1-n, m+j-i, dim)]=src[RIDX(m+j-i, n, dim)];
            }
        }
    }
}

/* 
 * rotate - Your current working version of rotate
 * IMPORTANT: This is the version you will be graded on
 */
char rotate_descr[] = "rotate: Current working version";
void rotate(int dim, pixel *src, pixel *dst) 
{
    naive_rotate(dim, src, dst);
}

/*********************************************************************
 * register_rotate_functions - Register all of your different versions
 *     of the rotate kernel with the driver by calling the
 *     add_rotate_function() for each test function. When you run the
 *     driver program, it will test and report the performance of each
 *     registered test function.  
 *********************************************************************/
char my_rotate_descr[] = "rotate: My working version";
void register_rotate_functions() 
{
    add_rotate_function(&naive_rotate, naive_rotate_descr);   
    add_rotate_function(&rotate, rotate_descr);   
    /* ... Register additional test functions here */
    add_rotate_function(&my_rotate, my_rotate_descr);
}


/***************
 * SMOOTH KERNEL
 **************/

/***************************************************************
 * Various typedefs and helper functions for the smooth function
 * You may modify these any way you like.
 **************************************************************/

/* A struct used to compute averaged pixel value */
typedef struct {
    int red;
    int green;
    int blue;
    int num;
} pixel_sum;

/* Compute min and max of two integers, respectively */
static int min(int a, int b) { return (a < b ? a : b); }
static int max(int a, int b) { return (a > b ? a : b); }

/* 
 * initialize_pixel_sum - Initializes all fields of sum to 0 
 */
static void initialize_pixel_sum(pixel_sum *sum) 
{
    sum->red = sum->green = sum->blue = 0;
    sum->num = 0;
    return;
}

/* 
 * accumulate_sum - Accumulates field values of p in corresponding 
 * fields of sum 
 */
static void accumulate_sum(pixel_sum *sum, pixel p) 
{
    sum->red += (int) p.red;
    sum->green += (int) p.green;
    sum->blue += (int) p.blue;
    sum->num++;
    return;
}

/* 
 * assign_sum_to_pixel - Computes averaged pixel value in current_pixel 
 */
static void assign_sum_to_pixel(pixel *current_pixel, pixel_sum sum) 
{
    current_pixel->red = (unsigned short) (sum.red/sum.num);
    current_pixel->green = (unsigned short) (sum.green/sum.num);
    current_pixel->blue = (unsigned short) (sum.blue/sum.num);
    return;
}

/* 
 * avg - Returns averaged pixel value at (i,j) 
 */
static pixel avg(int dim, int i, int j, pixel *src) 
{
    int ii, jj;
    pixel_sum sum;
    pixel current_pixel;

    initialize_pixel_sum(&sum);
    for(ii = max(i-1, 0); ii <= min(i+1, dim-1); ii++) 
	for(jj = max(j-1, 0); jj <= min(j+1, dim-1); jj++) 
	    accumulate_sum(&sum, src[RIDX(ii, jj, dim)]);

    assign_sum_to_pixel(&current_pixel, sum);
    return current_pixel;
}

/******************************************************
 * Your different versions of the smooth kernel go here
 ******************************************************/

/*
 * naive_smooth - The naive baseline version of smooth 
 */
char naive_smooth_descr[] = "naive_smooth: Naive baseline implementation";
void naive_smooth(int dim, pixel *src, pixel *dst) 
{
    int i, j;

    for (i = 0; i < dim; i++)
	for (j = 0; j < dim; j++)
	    dst[RIDX(i, j, dim)] = avg(dim, i, j, src);     /*smooth the (i,j)th pixel*/
}

char my_smooth_descr[] = "naive_smooth: My implementation";
void my_smooth(int dim, pixel *src, pixel *dst)
{
    int i,j;
    int k;
    // 对red、green、blue三部分分别处理
    // 四个角上的像素点元素，四个数参与平均，除 4 用右移 2 位代替
    dst[0].red = (src[0].red + src[1].red + src[dim].red + src[dim + 1].red) >> 2;
    dst[0].green = (src[0].green + src[1].green + src[dim].green + src[dim + 1].green) >> 2;
    dst[0].blue = (src[0].blue + src[1].blue + src[dim].blue + src[dim + 1].blue) >> 2;
    i = dim - 1;
    dst[i].red = (src[i].red + src[i - 1].red + src[i + dim].red + src[i + dim -1].red) >> 2;
    dst[i].green = (src[i].green + src[i - 1].green + src[i + dim].green + src[i + dim -1].green) >> 2;
    dst[i].blue = (src[i].blue + src[i - 1].blue + src[i + dim].blue + src[i + dim -1].blue) >> 2;
    i = i * dim;
    dst[i].red = (src[i].red + src[i + 1].red + src[i - dim].red + src[i - dim + 1].red) >> 2;
    dst[i].green = (src[i].green + src[i + 1].green + src[i - dim].green + src[i - dim + 1].green) >> 2;
    dst[i].blue = (src[i].blue + src[i + 1].blue + src[i - dim].blue + src[i - dim + 1].blue) >> 2;
    i = i + dim - 1;
    dst[i].red = (src[i].red + src[i - 1].red + src[i - dim].red + src[i - dim - 1].red) >> 2;
    dst[i].green = (src[i].green + src[i - 1].green + src[i - dim].green + src[i - dim - 1].green) >> 2;
    dst[i].blue = (src[i].blue + src[i - 1].blue + src[i - dim].blue + src[i - dim - 1].blue) >> 2;

    // 四个边上的像素点元素，四个数参与平均
    for (i = 1 ; i < dim - 1 ; i++) {
        dst[i].red = (src[i].red + src[i - 1].red + src[i + 1].red + src[i + dim].red + src[i + dim - 1].red + src[i + dim + 1].red) / 6;
        dst[i].green = (src[i].green + src[i - 1].green + src[i + 1].green + src[i + dim].green + src[i + dim - 1].green + src[i + dim + 1].green) / 6;
        dst[i].blue = (src[i].blue + src[i - 1].blue + src[i + 1].blue + src[i + dim].blue + src[i + dim - 1].blue + src[i + dim + 1].blue) / 6;
    }
    for (i = dim * (dim - 1) + 1 ; i < dim * dim - 1 ; i++) {
        dst[i].red = (src[i].red + src[i - 1].red + src[i + 1].red + src[i - dim].red + src[i - dim - 1].red + src[i - dim + 1].red) / 6;
        dst[i].green = (src[i].green + src[i - 1].green + src[i + 1].green + src[i - dim].green + src[i - dim - 1].green + src[i - dim + 1].green) / 6;
        dst[i].blue = (src[i].blue + src[i - 1].blue + src[i + 1].blue+ src[i - dim].blue + src[i - dim - 1].blue+ src[i - dim + 1].blue) / 6;
    }
    for (j = dim ; j < dim * (dim - 1) ; j+= dim) {
        dst[j].red = (src[j].red + src[j + 1].red + src[j - dim].red + src[j - dim + 1].red + src[j + dim].red + src[j + dim + 1].red) / 6;
        dst[j].green = (src[j].green + src[j + 1].green + src[j - dim].green+ src[j - dim + 1].green + src[j + dim].green + src[j + dim + 1].green) / 6;
        dst[j].blue = (src[j].blue + src[j + 1].blue + src[j - dim].blue + src[j - dim + 1].blue + src[j + dim].blue + src[j + dim + 1].blue) / 6;
    }
    for (j = 2 * dim - 1 ; j < dim * dim - 1 ; j += dim) {
        dst[j].red = (src[j].red + src[j - 1].red + src[j - dim].red + src[j - dim - 1].red + src[j + dim].red + src[j + dim - 1].red) / 6;
        dst[j].green = (src[j].green + src[j - 1].green + src[j - dim].green + src[j - dim - 1].green + src[j + dim].green + src[j + dim - 1].green) / 6;
        dst[j].blue = (src[j].blue + src[j - 1].blue + src[j - dim].blue + src[j - dim - 1].blue + src[j + dim].blue + src[j + dim - 1].blue) / 6;
    }

    // 剩余的像素点元素，九个数参与平均
    for (i = 1 ; i < dim - 1 ; i++) {
        for (j = 1 ; j < dim - 1 ; j++) {
            k = i * dim + j;
            dst[k].red = (src[k].red + src[k - 1].red + src[k + 1].red + src[k - dim].red + src[k - dim - 1].red + src[k - dim + 1].red + src[k + dim].red + src[k + dim - 1].red + src[k + dim + 1].red) / 9;
            dst[k].green = (src[k].green + src[k - 1].green + src[k + 1].green + src[k - dim].green + src[k - dim - 1].green + src[k - dim + 1].green + src[k + dim].green + src[k + dim - 1].green + src[k + dim + 1].green) / 9;
            dst[k].blue = (src[k].blue + src[k - 1].blue + src[k + 1].blue + src[k - dim].blue + src[k - dim - 1].blue + src[k - dim + 1].blue + src[k + dim].blue + src[k + dim - 1].blue + src[k + dim + 1].blue) / 9;
        }
    }
    
}

/*
 * smooth - Your current working version of smooth. 
 * IMPORTANT: This is the version you will be graded on
 */
char smooth_descr[] = "smooth: Current working version";
void smooth(int dim, pixel *src, pixel *dst) 
{
    naive_smooth(dim, src, dst);
}


/********************************************************************* 
 * register_smooth_functions - Register all of your different versions
 *     of the smooth kernel with the driver by calling the
 *     add_smooth_function() for each test function.  When you run the
 *     driver program, it will test and report the performance of each
 *     registered test function.  
 *********************************************************************/

void register_smooth_functions() {
    add_smooth_function(&smooth, smooth_descr);
    add_smooth_function(&naive_smooth, naive_smooth_descr);
    /* ... Register additional test functions here */
    add_smooth_function(&my_smooth, my_smooth_descr);
}

