(ns moon.draw.grid
  (:require [moon.draws :as draws]))

(defn do!
  [ctx leftx bottomy gridw gridh cellw cellh color-float-bits]
  (let [w (* (float gridw) (float cellw))
        h (* (float gridh) (float cellh))
        topy (+ (float bottomy) (float h))
        rightx (+ (float leftx) (float w))]
    (doseq [idx (range (inc (float gridw)))
            :let [linex (+ (float leftx) (* (float idx) (float cellw)))]]
      (draws/handle ctx
                    [[:draw/line [linex topy] [linex bottomy] color-float-bits]]))
    (doseq [idx (range (inc (float gridh)))
            :let [liney (+ (float bottomy) (* (float idx) (float cellh)))]]
      (draws/handle ctx
                    [[:draw/line [leftx liney] [rightx liney] color-float-bits]]))))
