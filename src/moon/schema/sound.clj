(ns moon.schema.sound
  (:require [gdl.scene2d.event :as event]
            [clj.api.com.badlogic.gdx.scenes.scene2d.group :as group]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as gdx-table]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.window :as gdx-window]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [gdl.viewport :as viewport]
            [moon.actor :as actor]
            [moon.scroll-pane-cell :as scroll-pane-cell]
            [moon.stage :as stage]
            [moon.table :as table]
            [moon.txs :as txs]
            [moon.window :as window]))

(defn malli-form [_ _schemas]
  :string)

(declare sound-columns)

(defn- rebuild-sound-widget! [table sound-name]
  (fn [actor {:keys [ctx/skin]}]
    (group/clear-children! table)
    (table/add-rows! table [(sound-columns skin table sound-name)])
    (actor/remove! (actor/find-ancestor actor :ui/window))
    (widget-group/pack! (actor/find-ancestor table :ui/window))
    (let [[k _] (actor/user-object table)]
      (actor/set-user-object! table [k sound-name]))))

(defn- open-select-sounds-handler [table]
  (fn [{:keys [ctx/audio
               ctx/skin
               ctx/stage]}]
    (stage/add-actor! stage
                      (doto (gdx-window/create "Choose" skin)
                        (window/add-close-button! skin)
                        (table/add-rows!
                         [[(scroll-pane-cell/create skin
                                                    (viewport/world-width (stage/viewport stage))
                                                    (for [sound-name (map first audio)]
                                                      [{:actor (doto (text-button/create sound-name skin)
                                                                 (actor/add-listener!
                                                                  (change-listener/create
                                                                   (fn [event actor]
                                                                     ((rebuild-sound-widget! table sound-name) actor (stage/ctx (event/stage event)))))))}
                                                       {:actor (doto (text-button/create "play!" skin)
                                                                 (actor/add-listener!
                                                                  (change-listener/create
                                                                   (fn [event _actor]
                                                                     (txs/handle! (stage/ctx (event/stage event))
                                                                                  [[:tx/sound sound-name]])))))}]))]])
                        (gdx-window/set-modal! true)
                        (widget-group/pack!)))))

(defn- sound-columns [skin table sound-name]
  [{:actor (doto (text-button/create sound-name skin)
             (actor/add-listener!
              (change-listener/create
               (fn [event _actor]
                 ((open-select-sounds-handler table) (stage/ctx (event/stage event)))))))}
   {:actor (doto (text-button/create "play!" skin)
             (actor/add-listener!
              (change-listener/create
               (fn [event _actor]
                 (txs/handle! (stage/ctx (event/stage event))
                              [[:tx/sound sound-name]])))))}])

(defn create [_  sound-name {:keys [ctx/skin]}]
  (let [table (doto (gdx-table/create)
                (table/set-cell-defaults! {:pad 5}))]
    (table/add-rows! table [(if sound-name
                              (sound-columns skin table sound-name)
                              [{:actor (doto (text-button/create "No sound" skin)
                                         (actor/add-listener!
                                          (change-listener/create
                                           (fn [event _actor]
                                             ((open-select-sounds-handler table) (stage/ctx (event/stage event)))))))}])])
    table))
