(ns moon.world)

(defprotocol World
  (dispose! [_])
  (tick-entities! [_])
  (remove-destroyed-entities! [_])
  (update-time [_ delta-ms])
  (blocked? [_ start target])
  (path-blocked? [_ start target path-w])
  (line-of-sight? [_ source target]))
