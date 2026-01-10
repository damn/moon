(ns gdl.math.frustum
  "A truncated rectangular pyramid. Used to define the viewable region and its projection onto the screen."
  (:import (com.badlogic.gdx.math Frustum)))

(defn plane-points
  "Eight points making up the near and far clipping \"rectangles\". order is counter clockwise, starting at bottom left."
  [^Frustum frustum]
  (.planePoints frustum))
