(ns clojure.com.badlogic.gdx.audio
  "This interface encapsulates the creation and management of audio resources. It allows you to get direct access to the audio
  hardware via the audio device and audio recorder interfaces, create sound effects via [[clojure.com.badlogic.gdx.audio.sound]]
  and play music streams via the music interface.
  
  All resources created via this interface have to be disposed as soon as they are no longer used.
  
  Note that all music instances will be automatically paused when the pause method is
  called, and automatically resumed when the resume method is called."
  (:import (com.badlogic.gdx Audio)
           (com.badlogic.gdx.files FileHandle)))

(defn new-sound
  "Creates a new [[clojure.com.badlogic.gdx.audio.sound]] which is used to play back audio effects such as gun shots or explosions. The sound's audio data
  is retrieved from the file specified via the [[clojure.com.badlogic.gdx.files.file-handle]]. Note that the complete audio data is loaded into RAM. You
  should therefore not load big audio files with this methods. The current upper limit for decoded audio is 1 MB.
  
  Currently supported formats are WAV, MP3 and OGG.
  
  The sound has to be disposed if it is no longer used via the [clojure.com.badlogic.gdx.utils.disposable/dispose!] method.
  
  Returns the new sound"
  [^Audio audio ^FileHandle file-handle]
  (.newSound audio file-handle))
