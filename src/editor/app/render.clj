(ns editor.app.render
  (:import (scene2d Stage)))

(defn render!
  [{:keys [ctx/stage]
    :as ctx}]
  (let [ctx (if-let [new-ctx (:stage/ctx stage)]
              new-ctx
              ctx)]
    (set! (.ctx stage) ctx)
    (Stage/.act stage)
    (Stage/.draw stage)
    (:stage/ctx stage)))
