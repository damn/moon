(ns editor.create-widget.sound
  (:require [clojure.gdx :as gdx]
            [editor.widget.sound.columns :refer [sound-columns]]
            [editor.widget.sound.open-select-sounds-handler :refer [open-select-sounds-handler]]
            [gdx.scenes.scene2d.ui.table :as table]
            [scene2d.ui.table.add-rows :refer [add-rows!]]
            [scene2d.ui.text-button :as text-button]
            [scene2d.utils.change-listener :as change-listener]))

(defn f
  [_  sound-name {:keys [ctx/skin]}]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-rows! table [(if sound-name
                        (sound-columns skin table sound-name)
                        [{:actor
                          (doto (text-button/create {:text "No sound" :skin skin})
                            (gdx/add-listener! (change-listener/create
                                                (fn [event _actor]
                                                  ((open-select-sounds-handler table)
                                                   (:stage/ctx (gdx/event-get-stage event))
                                                   sound-columns)))))}])])
    table))
