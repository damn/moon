(ns moon.impl.stage
  (:require [com.badlogic.gdx.math.vector2 :as vector2]
            [moon.stage :as stage]
            [moon.ui.actor :as actor]
            [moon.ui.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d CtxStage)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn create
  [{:keys [ctx/batch]}]
  (CtxStage. (FitViewport. 1440 900)
             batch))

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
    (.getWorldWidth (.getViewport stage)))

  (viewport-height [stage]
    (.getWorldHeight (.getViewport stage)))

  (update-viewport! [stage width height]
    (.update (.getViewport stage) width height true))

  (unproject [stage xy]
    (-> stage
        .getViewport
        (.unproject (vector2/->java xy))
        vector2/->clj)))
