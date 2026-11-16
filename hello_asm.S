/*
 * hello_asm.S - minimal FreeBSD kernel module in AArch64 assembly.
 *
 * Prints a message on kldload / kldunload. It is the hand-written
 * equivalent of:
 *
 *     static int hello_modevent(module_t m, int what, void *arg) {...}
 *     static moduledata_t hello_mod = { "hello_asm", hello_modevent, NULL };
 *     DECLARE_MODULE(hello_asm, hello_mod, SI_SUB_DRIVERS, SI_ORDER_MIDDLE);
 *     MODULE_VERSION(hello_asm, 1);
 *
 * VERIFY these against your tree's sys/module.h and sys/kernel.h:
 *   MDT_STRUCT_VERSION=1, MDT_MODULE=2, MDT_VERSION=1
 *   SI_SUB_DRIVERS=0x3100000, SI_ORDER_MIDDLE=0x1000000
 *   MOD_LOAD=0, MOD_UNLOAD=1, EOPNOTSUPP=45
 */

#define MDT_STRUCT_VERSION	1
#define MDT_MODULE		2
#define MDT_VERSION		1
#define SI_SUB_DRIVERS		0x3100000
#define SI_ORDER_MIDDLE		0x1000000
#define MOD_LOAD		0
#define MOD_UNLOAD		1
#define EOPNOTSUPP		45

	.section .rodata
mod_name:
	.asciz	"hello_asm"
msg_load:
	.asciz	"hello_asm: loaded (written in AArch64 assembly)\n"
msg_unload:
	.asciz	"hello_asm: unloaded\n"

	.text
	.align	2
	.globl	hello_modevent
	.type	hello_modevent, %function
/* int hello_modevent(module_t mod /*x0*/, int what /*w1*/, void *arg /*x2*/) */
hello_modevent:
	stp	x29, x30, [sp, #-16]!
	mov	x29, sp

	cmp	w1, #MOD_LOAD
	b.eq	1f
	cmp	w1, #MOD_UNLOAD
	b.eq	2f

	mov	w0, #EOPNOTSUPP
	b	9f
1:
	adrp	x0, msg_load
	add	x0, x0, :lo12:msg_load
	bl	printf
	mov	w0, #0
	b	9f
2:
	adrp	x0, msg_unload
	add	x0, x0, :lo12:msg_unload
	bl	printf
	mov	w0, #0
9:
	ldp	x29, x30, [sp], #16
	ret
	.size	hello_modevent, . - hello_modevent

/* ---- data structures the kernel linker expects ---- */

	.data
	.align	3
/* moduledata_t { const char *name; modeventhand_t evhand; void *priv; } */
hello_mod:
	.quad	mod_name
	.quad	hello_modevent
	.quad	0

/* struct mod_version { int mv_version; } */
	.align	2
hello_version:
	.word	1

/* struct sysinit { uint32 subsystem; uint32 order; func; udata; } */
	.align	3
hello_sysinit:
	.word	SI_SUB_DRIVERS
	.word	SI_ORDER_MIDDLE
	.quad	module_register_init
	.quad	hello_mod

/* struct mod_metadata { int version; int type; const void *data; const char *cname; } */
	.align	3
hello_md_module:
	.word	MDT_STRUCT_VERSION
	.word	MDT_MODULE
	.quad	hello_mod
	.quad	mod_name

hello_md_version:
	.word	MDT_STRUCT_VERSION
	.word	MDT_VERSION
	.quad	hello_version
	.quad	mod_name

/* Linker sets: each section holds an array of pointers to the objects above. */
	.pushsection set_sysinit_set, "aw", %progbits
	.align	3
	.quad	hello_sysinit
	.popsection

	.pushsection set_modmetadata_set, "aw", %progbits
	.align	3
	.quad	hello_md_module
	.quad	hello_md_version
	.popsection
