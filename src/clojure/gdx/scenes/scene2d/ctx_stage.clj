(ns clojure.gdx.scenes.scene2d.ctx-stage
  (:require [clojure.app :as app]
            [clojure.scene2d.actor :as actor]
            [clojure.scene2d.group :as group]
            [clojure.scene2d.stage :as stage]
            [clojure.utils.viewport :as viewport])
  (:import (clojure.lang ILookup)
           (com.badlogic.gdx.scenes.scene2d CtxStage)))

(.bindRoot #'app/stage
           (fn [viewport batch]
             (proxy [CtxStage ILookup] [viewport batch]
               (valAt [k]
                 (case k
                   ; TODO :stage/root
                   :stage/ctx      (.ctx         ^CtxStage this)
                   :stage/viewport (.getViewport ^CtxStage this))))))

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
