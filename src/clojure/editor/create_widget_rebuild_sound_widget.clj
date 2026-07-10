(ns clojure.editor.create-widget-rebuild-sound-widget
  (:require [clojure.scene2d.actor.find-ancestor :refer [find-ancestor]]
            [gdl.scenes.scene2d.actor :as actor]
            [clojure.ui.table.add-rows :refer [add-rows!]]
            [clojure.scene2d.group :as group]
            [clojure.pack! :as pack!]
            [gdl.scenes.scene2d.ui.window :as gdx-window]))

(defn rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (group/clear-children! table)
    (add-rows! table [(->sound-columns skin table sound-name)])
    (actor/remove-actor (find-ancestor actor (partial instance? gdx-window/class)))
    (pack!/f (find-ancestor table (partial instance? gdx-window/class)))
    (let [[k _] (actor/get-user-object table)]
      (actor/set-user-object table [k sound-name]))))
