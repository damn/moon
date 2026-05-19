(ns game.impl.stage
  (:require [com.badlogic.gdx.gdx :as gdx]
            [gdl.scene2d.stage :as stage]
            [gdl.scene2d.actor :as actor]
            [gdl.scene2d.group :as group]
            [gdl.utils.viewport :as viewport])
  (:import (clojure.lang ILookup)
           (com.badlogic.gdx.scenes.scene2d CtxStage)))

(defn create
  [{:keys [ctx/batch]}]
  (proxy [CtxStage ILookup] [(gdx/fit-viewport 1440 900) batch]
    (valAt [k]
      (case k
        ; TODO :stage/root
        :stage/ctx      (.ctx         ^CtxStage this)
        :stage/viewport (.getViewport ^CtxStage this)))))

(extend-type CtxStage
  stage/Stage
  (set-ctx! [stage ctx]
    (set! (.ctx stage) ctx))

  (add-actor! [stage actor]
    (.addActor stage (actor/create actor)))

  (act! [stage]
    (.act stage))

  (draw! [stage]
    (.draw stage))

  (find-actor [stage name]
    (-> stage
        .getRoot
        (group/find-actor name)))

  (mouseover-actor [stage position]
    (let [[x y] (-> stage :stage/viewport (viewport/unproject position))]
      (.hit stage x y true))))
