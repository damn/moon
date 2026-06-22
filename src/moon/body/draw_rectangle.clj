(ns moon.body.draw-rectangle)

(defn f
  [{:keys [body/position
           body/width
           body/height]}
   color-float-bits]
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    [[:draw/rectangle x y width height color-float-bits]]))
