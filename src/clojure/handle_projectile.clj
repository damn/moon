(ns clojure.handle-projectile
  (:require [clojure.projectile-start-point :as projectile-start-point]))

(defn f
  [[_ projectile] {:keys [effect/source effect/target-direction]} _ctx]
  [[:tx/spawn-projectile
    {:position (projectile-start-point/f (:entity/body @source)
                                         target-direction
                                         (:projectile/size projectile))
     :direction target-direction
     :faction (:entity/faction @source)}
    projectile]])
