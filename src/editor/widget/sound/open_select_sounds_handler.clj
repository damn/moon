(ns editor.widget.sound.open-select-sounds-handler
  (:require
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as gdx-window]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [scene2d.ui.scroll-pane :as scroll-pane]
            [scene2d.ui.window.add-close-button :as add-close-button]
            [editor.widget.sound.rebuild :refer [rebuild-sound-widget!]]
            [ctx.do :refer [do!]]
            [gdx.scenes.scene2d.ui.table :as table]
            [scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.window :as window]
            [scene2d.utils.change-listener :as change-listener]))

(defn open-select-sounds-handler [table ->sound-columns]
  (fn [{:keys [ctx/skin
               ctx/stage]
        :as ctx}]
    (stage/add-actor! stage
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
                                                                  (actor/add-listener! (change-listener/create
                                                                                   (fn [event actor]
                                                                                     ((rebuild-sound-widget! table sound-name ->sound-columns) actor (:stage/ctx (event/get-stage event)))))))}
                                                        {:actor (doto (text-button/create
                                                                       {:text "play!"
                                                                        :skin skin})
                                                                  (actor/add-listener! (change-listener/create
                                                                                   (fn [event _actor]
                                                                                     (do! (:stage/ctx (event/get-stage event))
                                                                                          [[:tx/sound sound-name]])))))}])} )]
                             {:actor (scroll-pane/create
                                      {:actor table
                                       :skin skin})
                              :width  (+ (actor/get-width table) 50)
                              :height (min (- (:viewport/world-height (:stage/viewport stage)) 50)
                                           (actor/get-height table))})]]})
                   (add-close-button/f! skin)
                   (gdx-window/set-modal! true)))))
