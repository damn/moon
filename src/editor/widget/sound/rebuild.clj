(ns editor.widget.sound.rebuild
  (:require [clojure.window :as window]
            [clojure.group :as group]
            [clojure.actor :as actor]
            [clojure.layout :as layout]
            [gdx.scene2d.actor.find-ancestor :refer [find-ancestor]]
            [gdx.scene2d.ui.table.add-rows :refer [add-rows!]]))

(defn rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (group/clear-children! table)
    (add-rows! table [(->sound-columns skin table sound-name)])
    (actor/remove! (find-ancestor actor (partial instance? window/class)))
    (layout/pack! (find-ancestor table (partial instance? window/class)))
    (let [[k _] (actor/get-user-object table)]
      (actor/set-user-object! table [k sound-name]))))
