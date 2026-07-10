(ns clojure.set-label-text-actor
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as gdx-label]))

(defn set-label-text-actor [label text-fn]
  (actor/new
   (fn [this _delta]
     (when-let [stage (actor/getStage this)]
       (gdx-label/setText label (text-fn (:stage/ctx stage)))))
   (fn [_actor _batch _parent-alpha])))
