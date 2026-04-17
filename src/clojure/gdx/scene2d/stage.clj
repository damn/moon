(ns clojure.gdx.scene2d.stage
  (:require [moon.stage]
            [clojure.scene2d.group :as group]
            [clojure.viewport :as viewport])
  (:import (clojure.gdx Stage)))

(defn create [viewport batch]
  (Stage. viewport batch))

(extend-type Stage
  moon.stage/Stage
  (ctx [stage]
    (.ctx stage))

  (set-ctx! [stage ctx]
    (set! (.ctx stage) ctx))

  (add-actor! [stage actor]
    (.addActor stage actor))

  (find-actor [stage name]
    (-> stage
        .getRoot
        (group/find-actor name)))

  (mouseover-actor [stage position]
    (let [[x y] (viewport/unproject (.getViewport stage) position)
          touchable? true]
      (.hit stage x y touchable?) ))

  (viewport [stage]
    (.getViewport stage))

  (act! [stage]
    (.act stage))

  (draw! [stage]
    (.draw stage)))
