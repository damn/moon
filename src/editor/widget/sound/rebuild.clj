(ns editor.widget.sound.rebuild
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.group.clear-children :as clear-children]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]
            [clojure.gdx.window.instance? :as window?]
            [scene2d.actor.find-ancestor :refer [find-ancestor]]
            [scene2d.ui.table.add-rows :refer [add-rows!]]))

(defn rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (clear-children/f table)
    (add-rows! table [(->sound-columns skin table sound-name)])
    (actor/remove! (find-ancestor actor window?/f))
    (layout/pack (find-ancestor table window?/f))
    (let [[k _] (actor/get-user-object table)]
      (actor/set-user-object! table [k sound-name]))))
