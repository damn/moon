(ns moon.impl.stage
  (:require [com.badlogic.gdx.math.vector2 :as vector2]
            [moon.stage :as stage]
            [moon.ui.actor :as actor]
            [moon.ui.group :as group])
  (:import (clojure.lang ILookup)
           (com.badlogic.gdx.scenes.scene2d CtxStage)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn create
  [{:keys [ctx/batch]}]
  (proxy [CtxStage ILookup] [(FitViewport. 1440 900) batch]
    (valAt [k]
      (case k
        :stage/viewport (.getViewport ^CtxStage this)))))

(extend-type CtxStage
  stage/Stage
  (ctx [stage]
    (.ctx stage))

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
    (let [[x y] (stage/unproject stage position)]
      (.hit stage x y true)))

  (viewport-width [stage]
    (FitViewport/.getWorldWidth (:stage/viewport stage)))

  (viewport-height [stage]
    (FitViewport/.getWorldHeight (:stage/viewport stage)))

  (update-viewport! [stage width height]
    (FitViewport/.update (:stage/viewport stage) width height true))

  (unproject [stage xy]
    (-> stage
        :stage/viewport
        (FitViewport/.unproject (vector2/->java xy))
        vector2/->clj)))
