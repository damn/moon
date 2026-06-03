(ns gdx.scenes.scene2d.ui.dev-menu.set-label-text-actor
  (:require [clojure.gdx.scene2d.actor.get-stage :refer [get-stage]]
            [clojure.gdx.scene2d.actor.create :as actor]
            [gdx.scenes.scene2d.ui.label :as label]))

(defn set-label-text-actor [label text-fn]
  (actor/create
   {:act! (fn [this _delta]
            (when-let [stage (get-stage this)]
              (label/set-text! label (text-fn (:stage/ctx stage)))))}))
