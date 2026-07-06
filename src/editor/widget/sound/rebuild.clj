(ns editor.widget.sound.rebuild
  (:require
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]
            [scene2d.actor.find-ancestor :refer [find-ancestor]]
            [scene2d.ui.table.add-rows :refer [add-rows!]]))

(defn rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (group/clear-children! table)
    (add-rows! table [(->sound-columns skin table sound-name)])
    (actor/remove! (find-ancestor actor (partial instance? (window/class))))
    (layout/pack! (find-ancestor table (partial instance? (window/class))))
    (let [[k _] (actor/get-user-object table)]
      (actor/set-user-object! table [k sound-name]))))
