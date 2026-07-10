(ns clojure.editor.render
  (:require [clojure.ctx.clear-screen :as clear-screen]
            [clojure.set-ctx :as set-ctx]
            [gdl.scenes.scene2d.stage :as stage]))

(defn render [{:keys [ctx/stage]
                     :as ctx}]
  (let [ctx (clear-screen/step ctx)
        ctx (if-let [new-ctx (:stage/ctx stage)]
              new-ctx
              ctx)]
    (set-ctx/f stage ctx)
    (stage/act! stage)
    (stage/draw! stage)
    (:stage/ctx stage)))
