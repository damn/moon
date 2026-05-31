(ns editor.widget.sound
  (:require [com.badlogic.gdx.scenes.scene2d.event :as event]
            [com.badlogic.gdx.scenes.scene2d.ui.scroll-pane :as scroll-pane]
            [editor.widget :as widget]
            [game.ctx :as ctx]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.group :as group]
            [gdx.scenes.scene2d.ui :as ui]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [gdx.scenes.scene2d.ui.window :as window]
            [gdx.stage :as stage]))

(declare sound-columns)

(defn- rebuild-sound-widget! [table sound-name]
  (fn [actor {:keys [ctx/skin]}]
    (group/clear-children! table)
    (table/add-rows! table [(sound-columns skin table sound-name)])
    (actor/remove! (actor/find-ancestor actor ui/window?))
    (widget-group/pack! (actor/find-ancestor table ui/window?))
    (let [[k _] (actor/user-object table)]
      (actor/set-user-object! table [k sound-name]))))

(defn- open-select-sounds-handler [table]
  (fn [{:keys [ctx/skin
               ctx/stage]
        :as ctx}]
    (stage/add-actor! stage
                      (window/create
                       {:title "Choose"
                        :skin skin
                        :window/close-button? skin
                        :window/modal? true
                        :table/rows
                        [[(let [table (table/create
                                       {:table/cell-defaults {:pad 5}
                                        :table/rows (for [sound-name (ctx/sound-names ctx)]
                                                      [{:actor (text-button/create
                                                                {:text sound-name
                                                                 :skin skin
                                                                 :actor/listeners {:listener/change
                                                                                   (fn [event actor]
                                                                                     ((rebuild-sound-widget! table sound-name) actor (:stage/ctx (event/stage event))))}})}
                                                       {:actor (text-button/create
                                                                {:text "play!"
                                                                 :skin skin
                                                                 :actor/listeners {:listener/change (fn [event _actor]
                                                                                                      (ctx/do! (:stage/ctx (event/stage event))
                                                                                                               [[:tx/sound sound-name]]))}})}])} )]
                            {:actor (scroll-pane/create
                                     {:actor table
                                      :skin skin})
                             :width  (+ (actor/width table) 50)
                             :height (min (- (:viewport/world-height (:stage/viewport stage)) 50)
                                          (actor/height table))})]]}))))

(defn- sound-columns [skin table sound-name]
  [{:actor (text-button/create
            {:text sound-name
             :skin skin
             :actor/listeners {:listener/change (fn [event _actor]
                                                  ((open-select-sounds-handler table) (:stage/ctx (event/stage event))))}})}
   {:actor (text-button/create
            {:text "play!"
             :skin skin
             :actor/listeners {:listener/change (fn [event _actor]
                                                  (ctx/do! (:stage/ctx (event/stage event))
                                                           [[:tx/sound sound-name]]))}})}])

(defmethod widget/create :s/sound [_  sound-name {:keys [ctx/skin]}]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (table/add-rows! table [(if sound-name
                              (sound-columns skin table sound-name)
                              [{:actor
                                (text-button/create
                                 {:text "No sound"
                                  :skin skin
                                  :actor/listeners {:listener/change
                                                    (fn [event _actor]
                                                      ((open-select-sounds-handler table) (:stage/ctx (event/stage event))))}})}])])
    table))
