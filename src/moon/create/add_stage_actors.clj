(ns moon.create.add-stage-actors
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn step
  [ctx actor-fns]
  (doseq [actor (map (fn [[f & params]] (apply f ctx params)) actor-fns)]
    (Stage/.addActor (:ctx/stage ctx) actor)
    #_(stage/add-actor! ctx actor)
    )
  ctx)
