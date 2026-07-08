(ns clojure.editor.create-widget-rebuild-sound-widget
  (:require [clojure.scene2d.actor.find-ancestor :refer [find-ancestor]]
            [clojure.scene2d.actor.get-user-object]
            [clojure.scene2d.actor.remove-actor]
            [clojure.scene2d.actor.set-user-object]
            [clojure.table.add-rows :refer [add-rows!]]
            [clojure.scene2d.group :as group]
            [clojure.pack! :as pack!]
            [clojure.window :as gdx-window]))

(defn rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (group/clear-children! table)
    (add-rows! table [(->sound-columns skin table sound-name)])
    (clojure.scene2d.actor.remove-actor/f (find-ancestor actor (partial instance? gdx-window/class)))
    (pack!/f (find-ancestor table (partial instance? gdx-window/class)))
    (let [[k _] (clojure.scene2d.actor.get-user-object/f table)]
      (clojure.scene2d.actor.set-user-object/f table [k sound-name]))))
