(ns moon.impl.stage
  (:require [clojure.gdx.utils.viewport :as viewport]
            [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [moon.stage :as stage]
            [moon.ui.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d CtxStage)))

(defn create
  [{:keys [ctx/batch]}]
  (CtxStage. (viewport/create 1440 900 (orthographic-camera/create))
             batch))

(extend-type CtxStage
  stage/Stage
  (ctx [stage]
    (.ctx stage))

  (set-ctx! [stage ctx]
    (set! (.ctx stage) ctx))

  (add-actor! [stage actor]
    (.addActor stage actor))

  (act! [stage]
    (.act stage))

  (draw! [stage]
    (.draw stage))

  (find-actor [stage name]
    (-> stage
        .getRoot
        (group/find-actor name)))

  (mouseover-actor [stage position]
    (let [[x y] (viewport/unproject (.getViewport stage) position)]
      (.hit stage x y true)))

  (viewport-width [stage]
    (viewport/world-width (.getViewport stage)))

  (viewport-height [stage]
    (viewport/world-height (.getViewport stage)))

  (update-viewport! [stage width height]
    (viewport/update! (.getViewport stage) width height true))

  (unproject [stage [x y]]
    (viewport/unproject (.getViewport stage) [x y])))
