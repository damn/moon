(ns editor.widget.sound.rebuild
  (:require [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.group :as group]
            [gdx.scenes.scene2d.ui :as ui]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.widget-group :as widget-group]))

(defn rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (group/clear-children! table)
    (table/add-rows! table [(->sound-columns skin table sound-name)])
    (actor/remove! (actor/find-ancestor actor ui/window?))
    (widget-group/pack! (actor/find-ancestor table ui/window?))
    (let [[k _] (actor/user-object table)]
      (actor/set-user-object! table [k sound-name]))))
