(ns ctx.add-stage-actors
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn f!
  [{:keys [ctx/stage]
    :as ctx}
   actor-fns]
  (doseq [[f & params] actor-fns]
    (Stage/.addActor stage (apply f ctx params))))
