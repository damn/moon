(ns gdl.graphics.orthographic-camera)

(defprotocol OrthographicCamera
  (combined [_])
  (frustum [_])
  (position [_])
  (zoom [_])
  (set-position! [_ [x y]])
  (set-zoom! [_ amount])
  (inc-zoom! [cam by])
  (calculate-zoom [_ {:keys [left top right bottom]}]
                  ;"calculates the zoom value for camera to see all the 4 points."

                  )
  (visible-tiles [_]))
