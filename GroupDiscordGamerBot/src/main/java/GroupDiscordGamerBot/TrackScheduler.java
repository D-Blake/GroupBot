package GroupDiscordGamerBot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class TrackScheduler extends AudioEventAdapter implements AudioEventListener {

    private AudioPlayer player;

    public TrackScheduler() {
    }

    public TrackScheduler(AudioPlayer p) {
        this.setPlayer(p);
    }

    public void onPlayerPause(AudioPlayer player) {
        // Player was paused
        player.setPaused(true);
    }

    public void onPlayerResume(AudioPlayer player) {
        // Player was resumed
        player.setPaused(false);
    }

    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        // A track started playing
        player.playTrack(track);
    }

    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            // Start next track
            player.playTrack(track);
        }
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    private void setPlayer(AudioPlayer player) {
        this.player = player;
    }

}
