(ns moon.impl.stage
  (:require [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [moon.stage :as stage]
            [moon.ui.actor :as actor]
            [moon.ui.group :as group]
            [moon.viewport :as viewport])
  (:import (clojure.lang ILookup)
           (com.badlogic.gdx.scenes.scene2d CtxStage)))

(defn create
  [{:keys [ctx/batch]}]
  (proxy [CtxStage ILookup] [(fit-viewport/create 1440 900) batch]
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
