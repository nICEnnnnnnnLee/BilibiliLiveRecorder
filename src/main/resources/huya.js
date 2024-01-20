function ye(e, t) {
  var i = (65535 & e) + (65535 & t);
  return (((e >> 16) + (t >> 16) + (i >> 16)) << 16) | (65535 & i);
}
function ve(e, t, i, s, a, r) {
  return ye(((n = ye(ye(t, e), ye(s, r))) << (o = a)) | (n >>> (32 - o)), i);
  var n, o;
}
function ge(e, t, i, s, a, r, n) {
  return ve((t & i) | (~t & s), e, t, a, r, n);
}
function Se(e, t, i, s, a, r, n) {
  return ve((t & s) | (i & ~s), e, t, a, r, n);
}
function Te(e, t, i, s, a, r, n) {
  return ve(t ^ i ^ s, e, t, a, r, n);
}
function Pe(e, t, i, s, a, r, n) {
  return ve(i ^ (t | ~s), e, t, a, r, n);
}
function Ee(e, t) {
  var i, s, a, r, n;
  (e[t >> 5] |= 128 << t % 32), (e[14 + (((t + 64) >>> 9) << 4)] = t);
  var o = 1732584193,
    h = -271733879,
    u = -1732584194,
    l = 271733878;
  for (i = 0; i < e.length; i += 16)
    (s = o),
      (a = h),
      (r = u),
      (n = l),
      (o = ge(o, h, u, l, e[i], 7, -680876936)),
      (l = ge(l, o, h, u, e[i + 1], 12, -389564586)),
      (u = ge(u, l, o, h, e[i + 2], 17, 606105819)),
      (h = ge(h, u, l, o, e[i + 3], 22, -1044525330)),
      (o = ge(o, h, u, l, e[i + 4], 7, -176418897)),
      (l = ge(l, o, h, u, e[i + 5], 12, 1200080426)),
      (u = ge(u, l, o, h, e[i + 6], 17, -1473231341)),
      (h = ge(h, u, l, o, e[i + 7], 22, -45705983)),
      (o = ge(o, h, u, l, e[i + 8], 7, 1770035416)),
      (l = ge(l, o, h, u, e[i + 9], 12, -1958414417)),
      (u = ge(u, l, o, h, e[i + 10], 17, -42063)),
      (h = ge(h, u, l, o, e[i + 11], 22, -1990404162)),
      (o = ge(o, h, u, l, e[i + 12], 7, 1804603682)),
      (l = ge(l, o, h, u, e[i + 13], 12, -40341101)),
      (u = ge(u, l, o, h, e[i + 14], 17, -1502002290)),
      (o = Se(
        o,
        (h = ge(h, u, l, o, e[i + 15], 22, 1236535329)),
        u,
        l,
        e[i + 1],
        5,
        -165796510
      )),
      (l = Se(l, o, h, u, e[i + 6], 9, -1069501632)),
      (u = Se(u, l, o, h, e[i + 11], 14, 643717713)),
      (h = Se(h, u, l, o, e[i], 20, -373897302)),
      (o = Se(o, h, u, l, e[i + 5], 5, -701558691)),
      (l = Se(l, o, h, u, e[i + 10], 9, 38016083)),
      (u = Se(u, l, o, h, e[i + 15], 14, -660478335)),
      (h = Se(h, u, l, o, e[i + 4], 20, -405537848)),
      (o = Se(o, h, u, l, e[i + 9], 5, 568446438)),
      (l = Se(l, o, h, u, e[i + 14], 9, -1019803690)),
      (u = Se(u, l, o, h, e[i + 3], 14, -187363961)),
      (h = Se(h, u, l, o, e[i + 8], 20, 1163531501)),
      (o = Se(o, h, u, l, e[i + 13], 5, -1444681467)),
      (l = Se(l, o, h, u, e[i + 2], 9, -51403784)),
      (u = Se(u, l, o, h, e[i + 7], 14, 1735328473)),
      (o = Te(
        o,
        (h = Se(h, u, l, o, e[i + 12], 20, -1926607734)),
        u,
        l,
        e[i + 5],
        4,
        -378558
      )),
      (l = Te(l, o, h, u, e[i + 8], 11, -2022574463)),
      (u = Te(u, l, o, h, e[i + 11], 16, 1839030562)),
      (h = Te(h, u, l, o, e[i + 14], 23, -35309556)),
      (o = Te(o, h, u, l, e[i + 1], 4, -1530992060)),
      (l = Te(l, o, h, u, e[i + 4], 11, 1272893353)),
      (u = Te(u, l, o, h, e[i + 7], 16, -155497632)),
      (h = Te(h, u, l, o, e[i + 10], 23, -1094730640)),
      (o = Te(o, h, u, l, e[i + 13], 4, 681279174)),
      (l = Te(l, o, h, u, e[i], 11, -358537222)),
      (u = Te(u, l, o, h, e[i + 3], 16, -722521979)),
      (h = Te(h, u, l, o, e[i + 6], 23, 76029189)),
      (o = Te(o, h, u, l, e[i + 9], 4, -640364487)),
      (l = Te(l, o, h, u, e[i + 12], 11, -421815835)),
      (u = Te(u, l, o, h, e[i + 15], 16, 530742520)),
      (o = Pe(
        o,
        (h = Te(h, u, l, o, e[i + 2], 23, -995338651)),
        u,
        l,
        e[i],
        6,
        -198630844
      )),
      (l = Pe(l, o, h, u, e[i + 7], 10, 1126891415)),
      (u = Pe(u, l, o, h, e[i + 14], 15, -1416354905)),
      (h = Pe(h, u, l, o, e[i + 5], 21, -57434055)),
      (o = Pe(o, h, u, l, e[i + 12], 6, 1700485571)),
      (l = Pe(l, o, h, u, e[i + 3], 10, -1894986606)),
      (u = Pe(u, l, o, h, e[i + 10], 15, -1051523)),
      (h = Pe(h, u, l, o, e[i + 1], 21, -2054922799)),
      (o = Pe(o, h, u, l, e[i + 8], 6, 1873313359)),
      (l = Pe(l, o, h, u, e[i + 15], 10, -30611744)),
      (u = Pe(u, l, o, h, e[i + 6], 15, -1560198380)),
      (h = Pe(h, u, l, o, e[i + 13], 21, 1309151649)),
      (o = Pe(o, h, u, l, e[i + 4], 6, -145523070)),
      (l = Pe(l, o, h, u, e[i + 11], 10, -1120210379)),
      (u = Pe(u, l, o, h, e[i + 2], 15, 718787259)),
      (h = Pe(h, u, l, o, e[i + 9], 21, -343485551)),
      (o = ye(o, s)),
      (h = ye(h, a)),
      (u = ye(u, r)),
      (l = ye(l, n));
  return [o, h, u, l];
}
function Ce(e) {
  var t,
    i = "",
    s = 32 * e.length;
  for (t = 0; t < s; t += 8)
    i += String.fromCharCode((e[t >> 5] >>> t % 32) & 255);
  return i;
}
function Ae(e) {
  var t,
    i = [];
  for (i[(e.length >> 2) - 1] = void 0, t = 0; t < i.length; t += 1) i[t] = 0;
  var s = 8 * e.length;
  for (t = 0; t < s; t += 8) i[t >> 5] |= (255 & e.charCodeAt(t / 8)) << t % 32;
  return i;
}
function ke(e) {
  var t,
    i,
    s = "";
  for (i = 0; i < e.length; i += 1)
    (t = e.charCodeAt(i)),
      (s +=
        "0123456789abcdef".charAt((t >>> 4) & 15) +
        "0123456789abcdef".charAt(15 & t));
  return s;
}
function De(e) {
  return unescape(encodeURIComponent(e));
}
function Ie(e) {
  return (function (e) {
    return Ce(Ee(Ae(e), 8 * e.length));
  })(De(e));
}
function Re(e, t) {
  return (function (e, t) {
    var i,
      s,
      a = Ae(e),
      r = [],
      n = [];
    for (
      r[15] = n[15] = void 0, a.length > 16 && (a = Ee(a, 8 * e.length)), i = 0;
      i < 16;
      i += 1
    )
      (r[i] = 909522486 ^ a[i]), (n[i] = 1549556828 ^ a[i]);
    return (
      (s = Ee(r.concat(Ae(t)), 512 + 8 * t.length)), Ce(Ee(n.concat(s), 640))
    );
  })(De(e), De(t));
}
var Oe = function (e, t, i) {
  return t ? (i ? Re(t, e) : ke(Re(t, e))) : i ? Ie(e) : ke(Ie(e));
};