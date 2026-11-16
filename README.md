# hello_asm

A minimal FreeBSD kernel module written in AArch64 assembly. It prints a
message to the kernel log when loaded and another when unloaded.

## Files

| File          | Purpose                                              |
|---------------|------------------------------------------------------|
| `hello_asm.S` | The module: event handler and registration structures |
| `Makefile`    | Builds the module with `bsd.kmod.mk`                 |

## Requirements

- FreeBSD on 64-bit ARM (arm64 / AArch64)
- Kernel source or headers installed (`/usr/src`)
- Root access to load and unload modules

## Build

```sh
make
```

This produces `hello_asm.ko`.

## Load and unload

```sh
sudo kldload ./hello_asm.ko
dmesg | tail -2
sudo kldunload hello_asm
dmesg | tail -2
```

Expected output:

```
hello_asm: loaded (written in AArch64 assembly)
hello_asm: unloaded
```

Check that the module is present with `kldstat | grep hello_asm`.

## How it works

The C macros `DECLARE_MODULE` and `MODULE_VERSION` normally generate the data
the kernel linker needs. Here they are written out by hand:

- `hello_mod` is a `moduledata_t`: module name, event handler, private data.
- `hello_sysinit` is a `struct sysinit` placed in the `set_sysinit_set` linker
  set. It makes the kernel call `module_register_init` when the module loads.
- `hello_md_module` and `hello_md_version` are `struct mod_metadata` entries
  placed in `set_modmetadata_set`. They let the linker find the module and
  its version.
- `hello_modevent` is the handler: `x0` is the module, `w1` is the event
  (`MOD_LOAD` = 0, `MOD_UNLOAD` = 1), `x2` is the argument. It calls
  `printf(9)` and returns 0, or `EOPNOTSUPP` for other events.

## Verify before use

The numeric constants at the top of `hello_asm.S` were written from memory:

- `MDT_STRUCT_VERSION`, `MDT_MODULE`, `MDT_VERSION` (see `sys/module.h`)
- `SI_SUB_DRIVERS`, `SI_ORDER_MIDDLE` (see `sys/kernel.h`)
- `MOD_LOAD`, `MOD_UNLOAD`, `EOPNOTSUPP`

Compare them with the headers in your source tree. Wrong values typically
show up as the module failing to load or register.

## Troubleshooting

- **`kldload` reports an undefined symbol:** the kernel version may differ
  from the headers you built against. Rebuild against the running kernel's
  source.
- **Module loads but prints nothing:** check `dmesg`, and confirm the
  `set_sysinit_set` entry is present with `objdump -h hello_asm.ko`.
- **Build fails on `.S`:** confirm you are on arm64. This code does not
  assemble for 32-bit ARM.

## License

Public domain / use freely.
