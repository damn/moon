(ns moon.world)

(defprotocol World
  (dispose! [_])
  (blocked? [_ start target])
  (path-blocked? [_ start target path-w])
  (line-of-sight? [_ source target]))
