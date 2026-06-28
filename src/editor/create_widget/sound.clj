(ns editor.create-widget.sound
  (:require [editor.widget.sound.columns :refer [sound-columns]]
            [editor.widget.sound.open-select-sounds-handler :refer [open-select-sounds-handler]]
            [gdx.scenes.scene2d.ui.table :as table]
            [scene2d.ui.table.add-rows :refer [add-rows!]]
            [scene2d.ui.text-button :as text-button]
            [scene2d.utils.change-listener :as change-listener])
  (:import (com.badlogic.gdx.scenes.scene2d Actor Event)))

(defn f
  [_  sound-name {:keys [ctx/skin]}]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-rows! table [(if sound-name
                        (sound-columns skin table sound-name)
                        [{:actor
                          (doto (text-button/create {:text "No sound" :skin skin})
                            (Actor/.addListener (change-listener/create
                                                 (fn [event _actor]
                                                   ((open-select-sounds-handler table)
                                                    (:stage/ctx (Event/.getStage event))
                                                    sound-columns)))))}])])
    table))
