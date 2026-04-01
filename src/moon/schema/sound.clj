(ns moon.schema.sound
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.group :as group]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as gdx-table]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.window :as gdx-window]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [clj.api.com.badlogic.gdx.utils.viewport :as viewport]
            [moon.actor :as actor]
            [moon.scroll-pane-cell :as scroll-pane-cell]
            [moon.stage :as stage]
            [moon.table :as table]
            [moon.text-button :as text-button]
            [moon.txs :as txs]
            [moon.window :as window])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Window)))

(defn malli-form [_ _schemas]
  :string)

(declare sound-columns)

(defn- rebuild-sound-widget! [table sound-name]
  (fn [actor {:keys [ctx/skin]}]
    (group/clear-children! table)
    (table/add-rows! table [(sound-columns skin table sound-name)])
    (actor/remove! (actor/find-ancestor actor Window))
    (widget-group/pack! (actor/find-ancestor table Window))
    (let [[k _] (actor/user-object table)]
      (actor/set-user-object! table [k sound-name]))))

(defn- open-select-sounds-handler [table]
  (fn [_actor {:keys [ctx/audio
                      ctx/skin
                      ctx/stage]}]
    (stage/add-actor! stage
                      (doto (gdx-window/create "Choose" skin)
                        (window/add-close-button! skin)
                        (table/add-rows!
                         [[(scroll-pane-cell/create skin
                                                    (viewport/world-width (stage/viewport stage))
                                                    (for [sound-name (map first audio)]
                                                      [{:actor (text-button/create
                                                                {:text sound-name
                                                                 :on-clicked (rebuild-sound-widget! table sound-name)
                                                                 :skin skin})}
                                                       {:actor (text-button/create
                                                                {:text "play!"
                                                                 :on-clicked (fn [_actor ctx]
                                                                               (txs/handle! ctx [[:tx/sound sound-name]]))
                                                                 :skin skin})}]))]])
                        (gdx-window/set-modal! true)
                        (widget-group/pack!)))))

(defn- sound-columns [skin table sound-name]
  [{:actor (text-button/create
            {:text sound-name
             :on-clicked (open-select-sounds-handler table)
             :skin skin})}
   {:actor (text-button/create
            {:text "play!"
             :on-clicked (fn [_actor ctx]
                           (txs/handle! ctx [[:tx/sound sound-name]]))
             :skin skin})}])

(defn create [_  sound-name {:keys [ctx/skin]}]
  (let [table (doto (gdx-table/create)
                (table/set-cell-defaults! {:pad 5}))]
    (table/add-rows! table [(if sound-name
                              (sound-columns skin table sound-name)
                              [{:actor (text-button/create
                                        {:text "No sound"
                                         :on-clicked (open-select-sounds-handler table)
                                         :skin skin})}])])
    table))
