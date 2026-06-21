(ns editor.widget.sound
  (:require [clojure.scenes.scene2d.actor.add-listener :refer [add-listener!]]
            [clojure.scenes.scene2d.event.get-stage :refer [get-stage]]
            [editor.widget.sound.columns :refer [sound-columns]]
            [editor.widget.sound.open-select-sounds-handler :refer [open-select-sounds-handler]]
            [gdx.scenes.scene2d.ui.table :as table]
            [clojure.scenes.scene2d.ui.table.add-rows :refer [add-rows!]]
            [clojure.scenes.scene2d.ui.text-button :as text-button]
            [clojure.scenes.scene2d.utils.change-listener :as change-listener]))

(defn create
  [_  sound-name {:keys [ctx/skin]}]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-rows! table [(if sound-name
                        (sound-columns skin table sound-name)
                        [{:actor
                          (doto (text-button/create {:text "No sound" :skin skin})
                            (add-listener! (change-listener/create
                                            (fn [event _actor]
                                              ((open-select-sounds-handler table)
                                               (:stage/ctx (get-stage event))
                                               sound-columns)))))}])])
    table))
