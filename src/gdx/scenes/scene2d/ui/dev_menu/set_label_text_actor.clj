(ns gdx.scenes.scene2d.ui.dev-menu.set-label-text-actor
  (:require [gdl.get-stage :refer [get-stage]]
            [gdl.actor :as actor]
            [gdl.label :as label]))

(defn set-label-text-actor [label text-fn]
  (actor/create
   {:act! (fn [this _delta]
            (when-let [stage (get-stage this)]
              (label/set-text! label (text-fn (:stage/ctx stage)))))}))
