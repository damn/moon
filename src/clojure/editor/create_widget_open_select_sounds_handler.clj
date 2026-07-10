(ns clojure.editor.create-widget-open-select-sounds-handler
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.ui.window.add-close-button :as add-close-button]
            [clojure.ui.table.add-rows :refer [add-rows!]]
            [clojure.moon.ctx-do :refer [do!]]
            [clojure.editor.create-widget-rebuild-sound-widget :as rebuild-sound-widget]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [clojure.scroll-pane-cell :as scroll-pane-cell]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]
            [clojure.ui-scroll-pane :as scroll-pane]
            [clojure.ui-table :as table]
            [clojure.ui-text-button :as text-button]
            [clojure.ui-window :as window]
            [clojure.scene2d.utils.change-listener :as change-listener]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as gdx-window]))

(defn open-select-sounds-handler [table ->sound-columns]
  (fn [{:keys [ctx/skin
               ctx/stage]
        :as ctx}]
    (stage/addActor stage
                      (doto (window/create
                             {:title "Choose"
                              :skin skin
                              :table/rows
                              [[(let [table (table/create
                                             {:table/cell-defaults {:pad 5}
                                              :table/rows (for [sound-name (map first (:ctx/audio ctx))]
                                                            [{:actor (doto (text-button/create
                                                                            {:text sound-name
                                                                             :skin skin})
                                                                           (actor/addListener (change-listener/create
                                                                                                    (fn [event actor]
                                                                                                      ((rebuild-sound-widget/rebuild-sound-widget! table sound-name ->sound-columns) actor (:stage/ctx (event/getStage event)))))))}
                                                             {:actor (doto (text-button/create
                                                                            {:text "play!"
                                                                             :skin skin})
                                                                           (actor/addListener (change-listener/create
                                                                                                    (fn [event _actor]
                                                                                                      (do! (:stage/ctx (event/getStage event))
                                                                                                           [[:tx/sound sound-name]])))))}])} )]
                                {:actor (scroll-pane/create
                                         {:actor table
                                          :skin skin})
                                 :width  (+ (actor/getWidth table) 50)
                                 :height (min (- (viewport/getWorldHeight (:stage/viewport stage)) 50)
                                              (actor/getHeight table))})]]})
                            (add-close-button/f! skin)
                            (gdx-window/setModal true)))))
