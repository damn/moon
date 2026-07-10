(ns clojure.editor.stage
  (:require [clojure.editor.main-window :as main-window]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [com.badlogic.gdx.input :as input]
            [clojure.scene2d-stage :as scene2d-stage]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]))

(defn f [{:keys [ctx/input
                ctx/batch] :as ctx}]
  (let [stage* (scene2d-stage/create (fit-viewport/new 1440 900) batch)]
    (input/setInputProcessor input stage*)
    (let [ctx (assoc ctx :ctx/stage stage*)]
      (stage/addActor (:ctx/stage ctx) (main-window/f ctx))
      ctx)))
