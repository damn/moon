(ns ctx.add-stage-actors
  (:import (scene2d Stage)
           (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f!
  [{:keys [ctx/stage]
    :as ctx}
   actor-fns]
  (doseq [[f & params] actor-fns]
    (Stage/.addActor stage (apply f ctx params))))
