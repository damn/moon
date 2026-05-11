(ns moon.raycaster)

(defprotocol Raycaster
  (blocked? [_ [start-x start-y] [target-x target-y]])
  (line-of-sight? [_ source target]))
