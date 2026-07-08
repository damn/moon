(ns clojure.editor.create.stage
  (:require [clojure.ctx-stage :as ctx-stage]
            [clojure.stage :as stage]
            [clojure.editor.create.stage.main-window :as main-window]))

(defn f [ctx]
  (let [ctx (assoc ctx :ctx/stage (ctx-stage/step ctx))]
    (stage/add-actor! (:ctx/stage ctx) (main-window/f ctx))
    ctx))
