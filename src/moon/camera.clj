(ns moon.camera)

(defprotocol Camera
  (zoom [_])
  (visible-tiles [_])
  )
