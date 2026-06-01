(ns editor.widget.sound
  (:require [clojure.gdx.scene2d.event :as event]
            [editor.widget :as widget]
            [editor.widget.sound.columns :refer [sound-columns]]
            [editor.widget.sound.open-select-sounds-handler :refer [open-select-sounds-handler]]
            [gdx.scenes.scene2d.ui.table :as table]
            [clojure.gdx.scene2d.ui.table.add-rows :refer [add-rows!]]
            [gdx.scenes.scene2d.ui.text-button :as text-button]))

(defmethod widget/create :s/sound [_  sound-name {:keys [ctx/skin]}]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-rows! table [(if sound-name
                        (sound-columns skin table sound-name)
                        [{:actor
                          (text-button/create
                           {:text "No sound"
                            :skin skin
                            :actor/listeners {:listener/change
                                              (fn [event _actor]
                                                ((open-select-sounds-handler table)
                                                 (:stage/ctx (event/stage event))
                                                 sound-columns))}})}])])
    table))
