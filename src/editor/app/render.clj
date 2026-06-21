(ns editor.app.render
  (:require [clojure.stage.set-ctx :refer [set-ctx!]]
            [clojure.stage.act :refer [act!]]
            [clojure.stage.draw :refer [draw!]]))

(defn render!
  [{:keys [ctx/stage]
    :as ctx}]
  (let [ctx (if-let [new-ctx (:stage/ctx stage)]
              new-ctx
              ctx)]
    (set-ctx! stage ctx)
    (act! stage)
    (draw! stage)
    (:stage/ctx stage)))
