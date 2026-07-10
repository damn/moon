(ns clojure.editor.create-widget-s-sound
  (:require 
            [clojure.table-set-opts :as table-set-opts]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.ui.table.add-rows :refer [add-rows!]]
            [clojure.editor.create-widget :refer [create-widget]]
            [clojure.editor.create-widget-open-select-sounds-handler :as open-select-sounds-handler]
            [clojure.editor.create-widget-sound-columns :as sound-columns]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]))

(defmethod create-widget :s/sound
  [_ sound-name {:keys [ctx/skin]}]
  (let [table (doto (table/new)
    (table-set-opts/set-opts! {:table/cell-defaults {:pad 5}}))]
    (letfn [(sound-columns-fn [skin table sound-name]
              (sound-columns/sound-columns skin table sound-name open-select-fn))
            (open-select-fn [table]
              (open-select-sounds-handler/open-select-sounds-handler table sound-columns-fn))]
      (add-rows! table [(if sound-name
                           (sound-columns-fn skin table sound-name)
                           [{:actor
                             (doto (text-button/new "No sound" skin)
                               (actor/addListener (change-listener/create
                                                        (fn [event _actor]
                                                          ((open-select-fn table)
                                                           (:stage/ctx (event/getStage event)))))))}])])
      table)))
