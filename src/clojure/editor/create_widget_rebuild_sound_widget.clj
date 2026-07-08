(ns clojure.editor.create-widget-rebuild-sound-widget
  (:require [clojure.actor.find-ancestor :refer [find-ancestor]]
            [clojure.actor.get-user-object]
            [clojure.actor.remove-actor]
            [clojure.actor.set-user-object]
            [clojure.add-rows :refer [add-rows!]]
            [clojure.group :as group]
            [clojure.pack! :as pack!]
            [clojure.window :as gdx-window]))

(defn rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (group/clear-children! table)
    (add-rows! table [(->sound-columns skin table sound-name)])
    (clojure.actor.remove-actor/f (find-ancestor actor (partial instance? gdx-window/class)))
    (pack!/f (find-ancestor table (partial instance? gdx-window/class)))
    (let [[k _] (clojure.actor.get-user-object/f table)]
      (clojure.actor.set-user-object/f table [k sound-name]))))
