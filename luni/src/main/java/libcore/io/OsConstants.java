/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package libcore.io;

public final class OsConstants {
    private OsConstants() { }

    public static boolean S_ISBLK(int mode) { return (mode & S_IFMT) == S_IFBLK; }
    public static boolean S_ISCHR(int mode) { return (mode & S_IFMT) == S_IFCHR; }
    public static boolean S_ISDIR(int mode) { return (mode & S_IFMT) == S_IFDIR; }
    public static boolean S_ISFIFO(int mode) { return (mode & S_IFMT) == S_IFIFO; }
    public static boolean S_ISREG(int mode) { return (mode & S_IFMT) == S_IFREG; }
    public static boolean S_ISLNK(int mode) { return (mode & S_IFMT) == S_IFLNK; }
    public static boolean S_ISSOCK(int mode) { return (mode & S_IFMT) == S_IFSOCK; }

    public static int WEXITSTATUS(int status) { return (status & 0xff00) >> 8; }
    public static boolean WCOREDUMP(int status) { return (status & 0x80) != 0; }
    public static int WTERMSIG(int status) { return status & 0x7f; }
    public static int WSTOPSIG(int status) { return WEXITSTATUS(status); }
    public static boolean WIFEXITED(int status) { return (WTERMSIG(status) == 0); }
    public static boolean WIFSTOPPED(int status) { return (WTERMSIG(status) == 0x7f); }
    public static boolean WIFSIGNALED(int status) { return (WTERMSIG(status + 1) >= 2); }

    public static final int AF_INET = get_AF_INET();
    private static native int get_AF_INET();
    public static final int AF_INET6 = get_AF_INET6();
    private static native int get_AF_INET6();
    public static final int AF_UNIX = get_AF_UNIX();
    private static native int get_AF_UNIX();
    public static final int AF_UNSPEC = get_AF_UNSPEC();
    private static native int get_AF_UNSPEC();
    public static final int E2BIG = get_E2BIG();
    private static native int get_E2BIG();
    public static final int EACCES = get_EACCES();
    private static native int get_EACCES();
    public static final int EADDRINUSE = get_EADDRINUSE();
    private static native int get_EADDRINUSE();
    public static final int EADDRNOTAVAIL = get_EADDRNOTAVAIL();
    private static native int get_EADDRNOTAVAIL();
    public static final int EAFNOSUPPORT = get_EAFNOSUPPORT();
    private static native int get_EAFNOSUPPORT();
    public static final int EAGAIN = get_EAGAIN();
    private static native int get_EAGAIN();
    public static final int EALREADY = get_EALREADY();
    private static native int get_EALREADY();
    public static final int EBADF = get_EBADF();
    private static native int get_EBADF();
    public static final int EBADMSG = get_EBADMSG();
    private static native int get_EBADMSG();
    public static final int EBUSY = get_EBUSY();
    private static native int get_EBUSY();
    public static final int ECANCELED = get_ECANCELED();
    private static native int get_ECANCELED();
    public static final int ECHILD = get_ECHILD();
    private static native int get_ECHILD();
    public static final int ECONNABORTED = get_ECONNABORTED();
    private static native int get_ECONNABORTED();
    public static final int ECONNREFUSED = get_ECONNREFUSED();
    private static native int get_ECONNREFUSED();
    public static final int ECONNRESET = get_ECONNRESET();
    private static native int get_ECONNRESET();
    public static final int EDEADLK = get_EDEADLK();
    private static native int get_EDEADLK();
    public static final int EDESTADDRREQ = get_EDESTADDRREQ();
    private static native int get_EDESTADDRREQ();
    public static final int EDOM = get_EDOM();
    private static native int get_EDOM();
    public static final int EDQUOT = get_EDQUOT();
    private static native int get_EDQUOT();
    public static final int EEXIST = get_EEXIST();
    private static native int get_EEXIST();
    public static final int EFAULT = get_EFAULT();
    private static native int get_EFAULT();
    public static final int EFBIG = get_EFBIG();
    private static native int get_EFBIG();
    public static final int EHOSTUNREACH = get_EHOSTUNREACH();
    private static native int get_EHOSTUNREACH();
    public static final int EIDRM = get_EIDRM();
    private static native int get_EIDRM();
    public static final int EILSEQ = get_EILSEQ();
    private static native int get_EILSEQ();
    public static final int EINPROGRESS = get_EINPROGRESS();
    private static native int get_EINPROGRESS();
    public static final int EINTR = get_EINTR();
    private static native int get_EINTR();
    public static final int EINVAL = get_EINVAL();
    private static native int get_EINVAL();
    public static final int EIO = get_EIO();
    private static native int get_EIO();
    public static final int EISCONN = get_EISCONN();
    private static native int get_EISCONN();
    public static final int EISDIR = get_EISDIR();
    private static native int get_EISDIR();
    public static final int ELOOP = get_ELOOP();
    private static native int get_ELOOP();
    public static final int EMFILE = get_EMFILE();
    private static native int get_EMFILE();
    public static final int EMLINK = get_EMLINK();
    private static native int get_EMLINK();
    public static final int EMSGSIZE = get_EMSGSIZE();
    private static native int get_EMSGSIZE();
    public static final int EMULTIHOP = get_EMULTIHOP();
    private static native int get_EMULTIHOP();
    public static final int ENAMETOOLONG = get_ENAMETOOLONG();
    private static native int get_ENAMETOOLONG();
    public static final int ENETDOWN = get_ENETDOWN();
    private static native int get_ENETDOWN();
    public static final int ENETRESET = get_ENETRESET();
    private static native int get_ENETRESET();
    public static final int ENETUNREACH = get_ENETUNREACH();
    private static native int get_ENETUNREACH();
    public static final int ENFILE = get_ENFILE();
    private static native int get_ENFILE();
    public static final int ENOBUFS = get_ENOBUFS();
    private static native int get_ENOBUFS();
    public static final int ENODATA = get_ENODATA();
    private static native int get_ENODATA();
    public static final int ENODEV = get_ENODEV();
    private static native int get_ENODEV();
    public static final int ENOENT = get_ENOENT();
    private static native int get_ENOENT();
    public static final int ENOEXEC = get_ENOEXEC();
    private static native int get_ENOEXEC();
    public static final int ENOLCK = get_ENOLCK();
    private static native int get_ENOLCK();
    public static final int ENOLINK = get_ENOLINK();
    private static native int get_ENOLINK();
    public static final int ENOMEM = get_ENOMEM();
    private static native int get_ENOMEM();
    public static final int ENOMSG = get_ENOMSG();
    private static native int get_ENOMSG();
    public static final int ENOPROTOOPT = get_ENOPROTOOPT();
    private static native int get_ENOPROTOOPT();
    public static final int ENOSPC = get_ENOSPC();
    private static native int get_ENOSPC();
    public static final int ENOSR = get_ENOSR();
    private static native int get_ENOSR();
    public static final int ENOSTR = get_ENOSTR();
    private static native int get_ENOSTR();
    public static final int ENOSYS = get_ENOSYS();
    private static native int get_ENOSYS();
    public static final int ENOTCONN = get_ENOTCONN();
    private static native int get_ENOTCONN();
    public static final int ENOTDIR = get_ENOTDIR();
    private static native int get_ENOTDIR();
    public static final int ENOTEMPTY = get_ENOTEMPTY();
    private static native int get_ENOTEMPTY();
    public static final int ENOTSOCK = get_ENOTSOCK();
    private static native int get_ENOTSOCK();
    public static final int ENOTSUP = get_ENOTSUP();
    private static native int get_ENOTSUP();
    public static final int ENOTTY = get_ENOTTY();
    private static native int get_ENOTTY();
    public static final int ENXIO = get_ENXIO();
    private static native int get_ENXIO();
    public static final int EOPNOTSUPP = get_EOPNOTSUPP();
    private static native int get_EOPNOTSUPP();
    public static final int EOVERFLOW = get_EOVERFLOW();
    private static native int get_EOVERFLOW();
    public static final int EPERM = get_EPERM();
    private static native int get_EPERM();
    public static final int EPIPE = get_EPIPE();
    private static native int get_EPIPE();
    public static final int EPROTO = get_EPROTO();
    private static native int get_EPROTO();
    public static final int EPROTONOSUPPORT = get_EPROTONOSUPPORT();
    private static native int get_EPROTONOSUPPORT();
    public static final int EPROTOTYPE = get_EPROTOTYPE();
    private static native int get_EPROTOTYPE();
    public static final int ERANGE = get_ERANGE();
    private static native int get_ERANGE();
    public static final int EROFS = get_EROFS();
    private static native int get_EROFS();
    public static final int ESPIPE = get_ESPIPE();
    private static native int get_ESPIPE();
    public static final int ESRCH = get_ESRCH();
    private static native int get_ESRCH();
    public static final int ESTALE = get_ESTALE();
    private static native int get_ESTALE();
    public static final int ETIME = get_ETIME();
    private static native int get_ETIME();
    public static final int ETIMEDOUT = get_ETIMEDOUT();
    private static native int get_ETIMEDOUT();
    public static final int ETXTBSY = get_ETXTBSY();
    private static native int get_ETXTBSY();
    public static final int EWOULDBLOCK = get_EWOULDBLOCK();
    private static native int get_EWOULDBLOCK();
    public static final int EXDEV = get_EXDEV();
    private static native int get_EXDEV();
    public static final int EXIT_FAILURE = get_EXIT_FAILURE();
    private static native int get_EXIT_FAILURE();
    public static final int EXIT_SUCCESS = get_EXIT_SUCCESS();
    private static native int get_EXIT_SUCCESS();
    public static final int FD_CLOEXEC = get_FD_CLOEXEC();
    private static native int get_FD_CLOEXEC();
    public static final int F_DUPFD = get_F_DUPFD();
    private static native int get_F_DUPFD();
    public static final int F_GETFD = get_F_GETFD();
    private static native int get_F_GETFD();
    public static final int F_GETFL = get_F_GETFL();
    private static native int get_F_GETFL();
    public static final int F_GETLK = get_F_GETLK();
    private static native int get_F_GETLK();
    public static final int F_GETOWN = get_F_GETOWN();
    private static native int get_F_GETOWN();
    public static final int F_OK = get_F_OK();
    private static native int get_F_OK();
    public static final int F_RDLCK = get_F_RDLCK();
    private static native int get_F_RDLCK();
    public static final int F_SETFD = get_F_SETFD();
    private static native int get_F_SETFD();
    public static final int F_SETFL = get_F_SETFL();
    private static native int get_F_SETFL();
    public static final int F_SETLK = get_F_SETLK();
    private static native int get_F_SETLK();
    public static final int F_SETLKW = get_F_SETLKW();
    private static native int get_F_SETLKW();
    public static final int F_SETOWN = get_F_SETOWN();
    private static native int get_F_SETOWN();
    public static final int F_UNLCK = get_F_UNLCK();
    private static native int get_F_UNLCK();
    public static final int F_WRLCK = get_F_WRLCK();
    private static native int get_F_WRLCK();
    public static final int IPPROTO_ICMP = get_IPPROTO_ICMP();
    private static native int get_IPPROTO_ICMP();
    public static final int IPPROTO_IP = get_IPPROTO_IP();
    private static native int get_IPPROTO_IP();
    public static final int IPPROTO_IPV6 = get_IPPROTO_IPV6();
    private static native int get_IPPROTO_IPV6();
    public static final int IPPROTO_RAW = get_IPPROTO_RAW();
    private static native int get_IPPROTO_RAW();
    public static final int IPPROTO_TCP = get_IPPROTO_TCP();
    private static native int get_IPPROTO_TCP();
    public static final int IPPROTO_UDP = get_IPPROTO_UDP();
    private static native int get_IPPROTO_UDP();
    public static final int MAP_FIXED = get_MAP_FIXED();
    private static native int get_MAP_FIXED();
    public static final int MAP_PRIVATE = get_MAP_PRIVATE();
    private static native int get_MAP_PRIVATE();
    public static final int MAP_SHARED = get_MAP_SHARED();
    private static native int get_MAP_SHARED();
    public static final int MCL_CURRENT = get_MCL_CURRENT();
    private static native int get_MCL_CURRENT();
    public static final int MCL_FUTURE = get_MCL_FUTURE();
    private static native int get_MCL_FUTURE();
    public static final int MSG_CTRUNC = get_MSG_CTRUNC();
    private static native int get_MSG_CTRUNC();
    public static final int MSG_DONTROUTE = get_MSG_DONTROUTE();
    private static native int get_MSG_DONTROUTE();
    public static final int MSG_EOR = get_MSG_EOR();
    private static native int get_MSG_EOR();
    public static final int MSG_OOB = get_MSG_OOB();
    private static native int get_MSG_OOB();
    public static final int MSG_PEEK = get_MSG_PEEK();
    private static native int get_MSG_PEEK();
    public static final int MSG_TRUNC = get_MSG_TRUNC();
    private static native int get_MSG_TRUNC();
    public static final int MSG_WAITALL = get_MSG_WAITALL();
    private static native int get_MSG_WAITALL();
    public static final int MS_ASYNC = get_MS_ASYNC();
    private static native int get_MS_ASYNC();
    public static final int MS_INVALIDATE = get_MS_INVALIDATE();
    private static native int get_MS_INVALIDATE();
    public static final int MS_SYNC = get_MS_SYNC();
    private static native int get_MS_SYNC();
    public static final int O_ACCMODE = get_O_ACCMODE();
    private static native int get_O_ACCMODE();
    public static final int O_APPEND = get_O_APPEND();
    private static native int get_O_APPEND();
    public static final int O_CREAT = get_O_CREAT();
    private static native int get_O_CREAT();
    public static final int O_EXCL = get_O_EXCL();
    private static native int get_O_EXCL();
    public static final int O_NOCTTY = get_O_NOCTTY();
    private static native int get_O_NOCTTY();
    public static final int O_NONBLOCK = get_O_NONBLOCK();
    private static native int get_O_NONBLOCK();
    public static final int O_RDONLY = get_O_RDONLY();
    private static native int get_O_RDONLY();
    public static final int O_RDWR = get_O_RDWR();
    private static native int get_O_RDWR();
    public static final int O_SYNC = get_O_SYNC();
    private static native int get_O_SYNC();
    public static final int O_TRUNC = get_O_TRUNC();
    private static native int get_O_TRUNC();
    public static final int O_WRONLY = get_O_WRONLY();
    private static native int get_O_WRONLY();
    public static final int PROT_EXEC = get_PROT_EXEC();
    private static native int get_PROT_EXEC();
    public static final int PROT_NONE = get_PROT_NONE();
    private static native int get_PROT_NONE();
    public static final int PROT_READ = get_PROT_READ();
    private static native int get_PROT_READ();
    public static final int PROT_WRITE = get_PROT_WRITE();
    private static native int get_PROT_WRITE();
    public static final int R_OK = get_R_OK();
    private static native int get_R_OK();
    public static final int SEEK_CUR = get_SEEK_CUR();
    private static native int get_SEEK_CUR();
    public static final int SEEK_END = get_SEEK_END();
    private static native int get_SEEK_END();
    public static final int SEEK_SET = get_SEEK_SET();
    private static native int get_SEEK_SET();
    public static final int SHUT_RD = get_SHUT_RD();
    private static native int get_SHUT_RD();
    public static final int SHUT_RDWR = get_SHUT_RDWR();
    private static native int get_SHUT_RDWR();
    public static final int SHUT_WR = get_SHUT_WR();
    private static native int get_SHUT_WR();
    public static final int SOCK_DGRAM = get_SOCK_DGRAM();
    private static native int get_SOCK_DGRAM();
    public static final int SOCK_RAW = get_SOCK_RAW();
    private static native int get_SOCK_RAW();
    public static final int SOCK_SEQPACKET = get_SOCK_SEQPACKET();
    private static native int get_SOCK_SEQPACKET();
    public static final int SOCK_STREAM = get_SOCK_STREAM();
    private static native int get_SOCK_STREAM();
    public static final int STDERR_FILENO = get_STDERR_FILENO();
    private static native int get_STDERR_FILENO();
    public static final int STDIN_FILENO = get_STDIN_FILENO();
    private static native int get_STDIN_FILENO();
    public static final int STDOUT_FILENO = get_STDOUT_FILENO();
    private static native int get_STDOUT_FILENO();
    public static final int S_IFBLK = get_S_IFBLK();
    private static native int get_S_IFBLK();
    public static final int S_IFCHR = get_S_IFCHR();
    private static native int get_S_IFCHR();
    public static final int S_IFDIR = get_S_IFDIR();
    private static native int get_S_IFDIR();
    public static final int S_IFIFO = get_S_IFIFO();
    private static native int get_S_IFIFO();
    public static final int S_IFLNK = get_S_IFLNK();
    private static native int get_S_IFLNK();
    public static final int S_IFMT = get_S_IFMT();
    private static native int get_S_IFMT();
    public static final int S_IFREG = get_S_IFREG();
    private static native int get_S_IFREG();
    public static final int S_IFSOCK = get_S_IFSOCK();
    private static native int get_S_IFSOCK();
    public static final int S_IRGRP = get_S_IRGRP();
    private static native int get_S_IRGRP();
    public static final int S_IROTH = get_S_IROTH();
    private static native int get_S_IROTH();
    public static final int S_IRUSR = get_S_IRUSR();
    private static native int get_S_IRUSR();
    public static final int S_IRWXG = get_S_IRWXG();
    private static native int get_S_IRWXG();
    public static final int S_IRWXO = get_S_IRWXO();
    private static native int get_S_IRWXO();
    public static final int S_IRWXU = get_S_IRWXU();
    private static native int get_S_IRWXU();
    public static final int S_ISGID = get_S_ISGID();
    private static native int get_S_ISGID();
    public static final int S_ISUID = get_S_ISUID();
    private static native int get_S_ISUID();
    public static final int S_ISVTX = get_S_ISVTX();
    private static native int get_S_ISVTX();
    public static final int S_IWGRP = get_S_IWGRP();
    private static native int get_S_IWGRP();
    public static final int S_IWOTH = get_S_IWOTH();
    private static native int get_S_IWOTH();
    public static final int S_IWUSR = get_S_IWUSR();
    private static native int get_S_IWUSR();
    public static final int S_IXGRP = get_S_IXGRP();
    private static native int get_S_IXGRP();
    public static final int S_IXOTH = get_S_IXOTH();
    private static native int get_S_IXOTH();
    public static final int S_IXUSR = get_S_IXUSR();
    private static native int get_S_IXUSR();
    public static final int WCONTINUED = get_WCONTINUED();
    private static native int get_WCONTINUED();
    public static final int WEXITED = get_WEXITED();
    private static native int get_WEXITED();
    public static final int WNOHANG = get_WNOHANG();
    private static native int get_WNOHANG();
    public static final int WNOWAIT = get_WNOWAIT();
    private static native int get_WNOWAIT();
    public static final int WSTOPPED = get_WSTOPPED();
    private static native int get_WSTOPPED();
    public static final int WUNTRACED = get_WUNTRACED();
    private static native int get_WUNTRACED();
    public static final int W_OK = get_W_OK();
    private static native int get_W_OK();
    public static final int X_OK = get_X_OK();
    private static native int get_X_OK();
}
