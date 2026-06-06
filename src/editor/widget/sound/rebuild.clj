(ns editor.widget.sound.rebuild
  (:require [gdx.scene2d.actor.get-user-object :refer [get-user-object]]
            [gdx.scene2d.actor.remove :refer [remove!]]
            [gdx.scene2d.actor.set-user-object :refer [set-user-object!]]
            [gdx.scene2d.actor.find-ancestor :refer [find-ancestor]]
            [gdx.scene2d.group.clear-children :refer [clear-children!]]
            [gdx.scene2d.ui.widget-group.pack :refer [pack!]]
            [gdx.scenes.scene2d.ui :as ui]
            [gdx.scene2d.ui.table.add-rows :refer [add-rows!]]))

(defn rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (clear-children! table)
    (add-rows! table [(->sound-columns skin table sound-name)])
    (remove! (find-ancestor actor ui/window?))
    (pack! (find-ancestor table ui/window?))
    (let [[k _] (get-user-object table)]
      (set-user-object! table [k sound-name]))))
