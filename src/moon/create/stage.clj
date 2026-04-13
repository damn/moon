(ns moon.create.stage
  (:require [gdl.scene2d.group :as group]
            [clj.api.moon.stage :as stage]
            [moon.stage]
            [gdl.viewport :as viewport])
  (:import (moon Stage)))

(defn step
  [{:keys [ctx/batch
           ctx/ui-viewport]
    :as ctx}]
  (assoc ctx :ctx/stage (stage/create ui-viewport batch)))

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
