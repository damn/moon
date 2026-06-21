(ns gdx.scenes.scene2d.ui.dev-menu.set-label-text-actor
  (:require [clojure.actor.get-stage :refer [get-stage]]
            [clojure.actor :as actor]
            [clojure.scenes.scene2d.ui.label :as label]))

(defn set-label-text-actor [label text-fn]
  (actor/create
   {:act! (fn [this _delta]
            (when-let [stage (get-stage this)]
              (label/set-text! label (text-fn (:stage/ctx stage)))))}))
