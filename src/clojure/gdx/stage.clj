(ns clojure.gdx.stage
  (:require [clj.api.clojure.gdx.stage :as stage]
            [moon.stage]
            [clojure.scene2d.group :as group]
            [clojure.viewport :as viewport])
  (:import (clojure.gdx Stage)))

(def create stage/create)

(extend-type Stage
  moon.stage/Stage
  (ctx [stage]
    (stage/ctx stage))

  (set-ctx! [stage ctx]
    (stage/set-ctx! stage ctx))

  (add-actor! [stage actor]
    (stage/add-actor! stage actor))

  (find-actor [stage name]
    (-> stage
        stage/root
        (group/find-actor name)))

  (mouseover-actor [stage position]
    (stage/hit stage
               (viewport/unproject (stage/viewport stage) position)
               true))

  (viewport [stage]
    (stage/viewport stage))

  (act! [stage]
    (stage/act! stage))

  (draw! [stage]
    (stage/draw! stage)))
