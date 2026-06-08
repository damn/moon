(ns effect.handle.projectile
  (:require [clojure.math.vector2.add :as add]
            [clojure.math.vector2.scale :as scale]
            [game.effect :as effect]))

(defn- proj-start-point [body direction size]
  (add/f (:body/position body)
         (scale/f direction
                  (+ (/ (:body/width body) 2) size 0.1))))

(defmethod effect/handle :effects/projectile
  [[_ projectile] {:keys [effect/source effect/target-direction]} _ctx]
  [[:tx/spawn-projectile
    {:position (proj-start-point (:entity/body @source)
                                 target-direction
                                 (:projectile/size projectile))
     :direction target-direction
     :faction (:entity/faction @source)}
    projectile]])
