(ns gdl.graphics.orthographic-camera)

(defprotocol OrthographicCamera
  (viewport-width [_])
  (viewport-height [_])
  (combined [_])
  (frustum [_])
  (position [_])
  (zoom [_])
  (set-position! [_ [x y]])
  (set-zoom! [_ amount]))

(defn inc-zoom! [cam by]
  (set-zoom! cam (max 0.1 (+ (zoom cam) by))))

(defn visible-tiles [camera]
  (let [[left-x right-x bottom-y top-y] (frustum camera)]
    (for [x (range (int left-x)   (int right-x))
          y (range (int bottom-y) (+ 2 (int top-y)))]
      [x y])))

(defn calculate-zoom
  "calculates the zoom value for camera to see all the 4 points."
  [camera {:keys [left top right bottom]}]
  (let [viewport-width  (viewport-width  camera)
        viewport-height (viewport-height camera)
        [px py] (position camera)
        px (float px)
        py (float py)
        leftx (float (left 0))
        rightx (float (right 0))
        x-diff (max (- px leftx) (- rightx px))
        topy (float (top 1))
        bottomy (float (bottom 1))
        y-diff (max (- topy py) (- py bottomy))
        vp-ratio-w (/ (* x-diff 2) viewport-width)
        vp-ratio-h (/ (* y-diff 2) viewport-height)
        new-zoom (max vp-ratio-w vp-ratio-h)]
    new-zoom))
